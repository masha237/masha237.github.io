package com.example.vk.service;

import com.example.vk.domain.File;
import com.example.vk.domain.User;
import com.example.vk.domain.UserInfo;
import com.example.vk.exception.ValidationException;
import com.example.vk.form.LoginForm;
import com.example.vk.form.RegisterForm;
import com.example.vk.repository.UserRepository;
import com.example.vk.utils.FileUploadUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {
    UserRepository userRepository;
    private static final String PASSWORD_SALT = "secret salt";


    public UserService(UserRepository userRepository) {

        this.userRepository = userRepository;

    }

    public List<User> getPeople() {
        return userRepository.findAll();
    }

    public Optional<User> findById(long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByLoginAndPassword(LoginForm loginForm) {
        return  userRepository.findByLoginAndPassword(loginForm.getLogin(), getPasswordSha(loginForm.getPassword()));
    }

    public Optional<User> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    public User register(RegisterForm registerForm) {
        if (!userRepository.findByLogin(registerForm.getLogin()).isEmpty()) {
            throw new ValidationException("Not unique login");
        }
        if (registerForm.getAge() < 0) {
            throw new ValidationException("Invalid age");
        }
        if (registerForm.getCity().length() > 100) {
            throw new ValidationException("City too long");
        }
        if (registerForm.getUniversity().length() > 100) {
            throw new ValidationException("University too long");
        }
        if (registerForm.getUsername().length() > 100) {
            throw new ValidationException("Name too long");
        }
        if (registerForm.getLogin().length() > 100) {
            throw new ValidationException("Login too long");
        }
        if (registerForm.getPassword().length() > 256) {
            throw new ValidationException("Password too long");
        }
        if (registerForm.getAvatar() != null && registerForm.getAvatar().getName().length() > 100) {
            throw new ValidationException("File name too long");
        }



        User newUser = new User();
        UserInfo newUserInfo = new UserInfo();
        newUserInfo.setAge(registerForm.getAge());
        newUserInfo.setCity(registerForm.getCity());
        newUserInfo.setUniversity(registerForm.getUniversity());
        newUserInfo.setUsername(registerForm.getUsername());
        if (registerForm.getAvatar() != null) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(registerForm.getAvatar().getOriginalFilename()));
            newUserInfo.setFileName(fileName);
        }
        newUser.setLogin(registerForm.getLogin());
        newUser.setPassword(getPasswordSha(registerForm.getPassword()));
        newUser.setUserInfo(newUserInfo);
        try {
            User savedUser = userRepository.save(newUser);

            String uploadDir = "user-photos/" + savedUser.getLogin();
            if (newUserInfo.getFileName() != null) {
                FileUploadUtil.saveFile(uploadDir, newUserInfo.getFileName(), registerForm.getAvatar());
            }
        } catch (Exception e) {
            throw new ValidationException("Register failed", e);
        }
        return newUser;
    }

    public File sendFile(String login, String fileName) throws IOException {
        return FileUploadUtil.sendFile("user-photos/" + login, fileName);
    }

    public void addFriend(User first, User second) {
        first.getFriends().add(second);
        first.getFriendOf().add(second);
        userRepository.save(first);
    }

    public void deleteFriend(User first, User second) {
        first.getFriends().remove(second);
        first.getFriendOf().remove(second);
        userRepository.save(first);
    }

    private String getPasswordSha(String password) {
        return /*Hashing.sha256().hashBytes((PASSWORD_SALT + password).getBytes(StandardCharsets.UTF_8)).toString()*/ password;
    }
}
