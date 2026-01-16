package com.backend_fullstep.service.impl;

import com.backend_fullstep.common.UserStatus;
import com.backend_fullstep.controller.request.UserCreationRequest;
import com.backend_fullstep.controller.request.UserPasswordRequest;
import com.backend_fullstep.controller.request.UserUpdateRequest;
import com.backend_fullstep.controller.response.UserPageResponse;
import com.backend_fullstep.controller.response.UserResponse;
import com.backend_fullstep.exception.BadRequestException;
import com.backend_fullstep.exception.InvalidDataException;
import com.backend_fullstep.exception.ResourceNotFoundException;
import com.backend_fullstep.model.AddressEntity;
import com.backend_fullstep.model.UserEntity;
import com.backend_fullstep.repository.AddressRepository;
import com.backend_fullstep.repository.UserRepository;
import com.backend_fullstep.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j (topic = "USER-SERVICE")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    public UserPageResponse findAll(String keyword, String sort, int page, int size) {
        log.info("findAll start");

        //Sorting
        Sort.Order order = new Sort.Order(Sort.Direction.ASC, "id");
        if(StringUtils.hasLength(sort)){
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)}"); /// tenncot:asc|desc
            Matcher matcher = pattern.matcher(sort);
            if(matcher.find()){
                String columnName = matcher.group(1);
                if(matcher.group(3).equalsIgnoreCase("asc")){
                    order = new Sort.Order(Sort.Direction.ASC, columnName);
                }else {
                    order = new Sort.Order(Sort.Direction.DESC, columnName);
                }
            }
        }

        // Xu ly truong hop FE muon bat dau voi page = 1

        int pageNo = 0;
        if(page > 0){
            pageNo = page - 1;
        }

        //Paging

        Pageable pageable = PageRequest.of(pageNo, size, Sort.by(order));

        Page<UserEntity> entityPage;

        if(StringUtils.hasLength(keyword)){
            keyword = "%" + keyword.toLowerCase() +"%";
            entityPage = userRepository.searchByKeyword(keyword, pageable);
        } else {
            entityPage = userRepository.findAll(pageable);
        }

        List<UserResponse> userList = entityPage.stream().map(entity -> UserResponse.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .gender(entity.getGender())
                .birthday(entity.getBirthday())
                .userName(entity.getUserName())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .build())
                .toList();

        UserPageResponse response = new UserPageResponse();
        response.setPageNumber(entityPage.getNumber());
        response.setPageSize(entityPage.getSize());
        response.setTotalElements(entityPage.getTotalElements());
        response.setTotalPage(entityPage.getTotalPages());
        response.setUsers(userList);

        return response;
    }

    @Override
    public UserResponse findById(Long id) {
        log.info("Find user by id: {}", id);

        UserEntity userEntity = getUserEntity(id);

        return UserResponse.builder()
                .id(id)
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .gender(userEntity.getGender())
                .birthday(userEntity.getBirthday())
                .email(userEntity.getEmail())
                .phone(userEntity.getPhone())
                . build();
    }

    @Override
    public UserResponse findByUserName(String name) {
        return null;
    }

    @Override
    public UserResponse findByEmail(String email) {
        return null;
    }

    @Override
    @Transactional(rollbackFor =  Exception.class)
    public long save(UserCreationRequest req) {

        log.info("Saving user: {}", req);
//        UserEntity userByEmail = userRepository.findByEmail(req.getEmail());
//        if(userByEmail!=null){
//            throw new InvalidDataException("Email already exists");
//        }

        UserEntity user = new UserEntity();
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setGender(req.getGender());
        user.setBirthday(req.getBirthday());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setUserName(req.getUsername());
        user.setType(req.getType());
        user.setStatus(UserStatus.NONE);

        UserEntity result = userRepository.save(user);
        log.info("Saved user: {}", user);

        if(result.getId()!=null){
            log.info("user id: {}", result.getId());
            List<AddressEntity> addresses = new ArrayList<>();
            req.getAddresses().forEach(addressRequest -> {
                AddressEntity addressEntity = new AddressEntity();
                addressEntity.setApartmentNumber(addressRequest.getApartmentNumber());
                addressEntity.setFloor(addressRequest.getFloor());
                addressEntity.setBuilding(addressRequest.getBuilding());
                addressEntity.setStreetNumber(addressRequest.getStreetNumber());
                addressEntity.setStreet(addressRequest.getStreet());
                addressEntity.setCity(addressRequest.getCity());
                addressEntity.setCountry(addressRequest.getCountry());
                addressEntity.setAddressType(addressRequest.getAddressType());
                addressEntity.setUserId(result.getId());
                addresses.add(addressEntity);
            });
            addressRepository.saveAll(addresses);
            log.info("Saved addresses: {}", addresses);
        }
        //Send email verification
        try {
            emailService.sendVerificationEmail(req.getEmail(), req.getUsername());
        } catch (IOException e) {
            throw new InvalidDataException("Send email failed");
        }
        return result.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UserUpdateRequest req) {
        log.info("Updating user: {}", req);

            UserEntity userEntity = getUserEntity(req.getId());

            userEntity.setFirstName(req.getFirstName());
            userEntity.setLastName(req.getLastName());
            userEntity.setGender(req.getGender());
            userEntity.setBirthday(req.getBirthday());
            userEntity.setEmail(req.getEmail());
            userEntity.setPhone(req.getPhone());
            userEntity.setUserName(req.getUsername());

            userRepository.save(userEntity);
            log.info("Updated user: {}", userEntity);

            //save address
        List<AddressEntity> addresses = new ArrayList<>();
        req.getAddresses().forEach(addressRequest -> {
            AddressEntity addressEntity = addressRepository.findByUserIdAndAddressType(req.getId(), addressRequest.getAddressType());
            if(addressEntity == null){
                 addressEntity = new AddressEntity();
            }
            addressEntity.setApartmentNumber(addressRequest.getApartmentNumber());
            addressEntity.setFloor(addressRequest.getFloor());
            addressEntity.setBuilding(addressRequest.getBuilding());
            addressEntity.setStreetNumber(addressRequest.getStreetNumber());
            addressEntity.setStreet(addressRequest.getStreet());
            addressEntity.setCity(addressRequest.getCity());
            addressEntity.setCountry(addressRequest.getCountry());
            addressEntity.setAddressType(addressRequest.getAddressType());
            addressEntity.setUserId(userEntity.getId());

            addresses.add(addressEntity);
        });

        // save addresses
        addressRepository.saveAll(addresses);
        log.info("Updated addresses: {}", addresses);



    }

    @Override
    public void changePassword(UserPasswordRequest req) {
        log.info("Changing password for user: {}", req);
        UserEntity userEntity = getUserEntity(req.getId());
        if (!Objects.equals(req.getPassword(), req.getConfirmPassword())) {
            throw new BadRequestException("Password and confirm password do not match");
        }
        userEntity.setPassword(passwordEncoder.encode(req.getPassword()));
        userRepository.save(userEntity);
        log.info("Changed password for user: {}", userEntity);
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting use: {}", id);
        UserEntity userEntity = getUserEntity(id);
        userEntity.setStatus(UserStatus.INACTIVE);
        userRepository.save(userEntity);
        log.info("Deleted user id: {}", id);
    }

    /**
     * Get user by id
     * @param id
     * @return
     */
    private UserEntity getUserEntity (Long id){
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
