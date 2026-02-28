package pet.project.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pet.project.database.entity.User;
import pet.project.database.repository.UserRepository;
import pet.project.dto.UserDto;
import pet.project.mapper.UserCreateMapper;

import java.util.Collections;


@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserCreateMapper userCreateMapper;

    @Transactional
    public User save(UserDto userDto) {
        User user = userCreateMapper.mapFrom(userDto);

        return userRepository.save(user);
    }

    public User findById(Long id){
        return userRepository.findById(id).orElseThrow();
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

    @Transactional
    public int updateUserPassword(UserDto userDto) {
        User user = userCreateMapper.mapFrom(userDto);

        return userRepository.updateUserPassword(user.getPassword(), user.getUsername());
    }
}
