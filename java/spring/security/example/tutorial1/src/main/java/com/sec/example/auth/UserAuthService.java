package com.sec.example.auth;

	import java.util.Arrays;
	import java.util.List;
	import java.util.Optional;
	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.security.core.authority.SimpleGrantedAuthority;
	import org.springframework.security.core.userdetails.User;
	import org.springframework.security.core.userdetails.UserDetails;
	import org.springframework.security.core.userdetails.UserDetailsService;
	import org.springframework.security.core.userdetails.UsernameNotFoundException;
	import org.springframework.security.crypto.password.PasswordEncoder;
	import org.springframework.stereotype.Service;

@Service
public class UserAuthService implements UserDetailsService {

	@Autowired
	private UserModelRepository userModelRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<UserModel> user = userModelRepository.findByUsername(username);
		UserModel matchedUserModel = user.orElseThrow(()->new UsernameNotFoundException(username));

		String matchedUserName = matchedUserModel.getUsername();
		String matchedUserPw = matchedUserModel.getPassword();

		List<SimpleGrantedAuthority> authorities
			= Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));

		return new User(matchedUserName, matchedUserPw, authorities);
	}

	public UserModel createUser(String username, String password){
		UserModel userModel = new UserModel();
		userModel.setUsername(username);
		userModel.setPassword(passwordEncoder.encode(password)); // 이 부분이 변경되었다.
		return userModelRepository.save(userModel);
	}

}
