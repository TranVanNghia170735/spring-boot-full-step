package com.backend_fullstep.service.impl;

import com.backend_fullstep.common.UserStatus;
import com.backend_fullstep.controller.request.UserCreationRequest;
import com.backend_fullstep.controller.request.UserPasswordRequest;
import com.backend_fullstep.controller.request.UserUpdateRequest;
import com.backend_fullstep.controller.response.UserResponse;
import com.backend_fullstep.exception.ResourceNotFoundException;
import com.backend_fullstep.model.AddressEntity;
import com.backend_fullstep.model.UserEntity;
import com.backend_fullstep.repository.AddressRepository;
import com.backend_fullstep.repository.UserRepository;
import com.backend_fullstep.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j (topic = "USER-SERVICE")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserResponse> findAll() {
        return List.of();
    }

    @Override
    public UserResponse findById(Long id) {
        return null;
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
        UserEntity user = new UserEntity();
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setGender(req.getGender());
        user.setBirthday(req.getBirthday());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setUsername(req.getUsername());
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
        return result.getId();
    }

    @Override
    public void update(UserUpdateRequest req) {
        log.info("Updating user: {}", req);

            UserEntity userEntity = getUserEntity(req.getId());

            userEntity.setFirstName(req.getFirstName());
            userEntity.setLastName(req.getLastName());
            userEntity.setGender(req.getGender());
            userEntity.setBirthday(req.getBirthday());
            userEntity.setEmail(req.getEmail());
            userEntity.setPhone(req.getPhone());
            userEntity.setUsername(req.getUsername());

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
        if(req.getPassword().equals(req.getConfirmPassword())){
            userEntity.setPassword(passwordEncoder.encode(req.getPassword()));
        }
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
