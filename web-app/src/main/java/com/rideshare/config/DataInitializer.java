package com.rideshare.config;

import com.rideshare.model.College;
import com.rideshare.model.User;
import com.rideshare.repository.CollegeRepository;
import com.rideshare.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CollegeRepository collegeRepository;
    private final UserRepository userRepository;

    public DataInitializer(CollegeRepository collegeRepository, UserRepository userRepository) {
        this.collegeRepository = collegeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        if (collegeRepository.count() > 0) return;

        College pict = collegeRepository.save(new College("Pune Institute of Computer Technology", "PICT", "Dhankawadi, Pune", "020-24371101"));
        College coep = collegeRepository.save(new College("College of Engineering Pune", "COEP", "Shivajinagar, Pune", "020-25507000"));
        College vit = collegeRepository.save(new College("Vishwakarma Institute of Technology", "VIT", "Bibwewadi, Pune", "020-24202124"));
        collegeRepository.save(new College("MIT World Peace University", "MITWPU", "Kothrud, Pune", "020-30273400"));
        collegeRepository.save(new College("Symbiosis Institute of Technology", "SIT", "Lavale, Pune", "020-39116100"));

        String hash = BCrypt.hashpw("password123", BCrypt.gensalt());
        userRepository.save(new User("PICT001", hash, "Rahul Sharma", "rahul@pict.edu", "9876543210", pict));
        userRepository.save(new User("COEP002", hash, "Priya Patil", "priya@coep.edu", "9876543211", coep));
        userRepository.save(new User("VIT003", hash, "Amit Deshmukh", "amit@vit.edu", "9876543212", vit));
    }
}
