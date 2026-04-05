package pet.project.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pet.project.database.entity.User;
import pet.project.database.repository.UserRepository;
import pet.project.dto.UserDto;
import pet.project.exception.UserNotFoundException;
import pet.project.exception.UserRefreshRatingFailedException;
import pet.project.mapper.UserCreateMapper;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserCreateMapper userCreateMapper;
    private final PasswordEncoder passwordEncoder;

    public User save(UserDto userDto) {
        User user = userCreateMapper.mapFrom(userDto);

        return userRepository.save(user);
    }

    public User findById(Long id){
        return userRepository.findById(id).orElseThrow(() ->new UserNotFoundException(id));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.loadUserByUsername(username)
                .map(user -> new org.springframework.security.core.userdetails.User(
                        user.getUsername(),
                        user.getPassword(),
                        Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
                ))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public int updateUserPassword(UserDto userDto) {
        String username = userDto.getUsername();
        String password = passwordEncoder.encode(userDto.getPassword());

        return userRepository.updateUserPassword(username, password);
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void refreshAllUserRatings(){
        try{
            userRepository.refreshAllUserRatings();
        }catch (Exception e){
            log.error("User refresh failed: {}",e.getMessage());

            throw new UserRefreshRatingFailedException();
        }
    }

}