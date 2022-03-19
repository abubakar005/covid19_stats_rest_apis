package covid19.statistics.reports.service;

import covid19.statistics.reports.util.CustomUtil;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

        String password = CustomUtil.usersList.get(userName);

        if(password == null)
            throw new UsernameNotFoundException("No dto found with the name " + userName);

        return new User(userName, password, new ArrayList<>());
    }
}