package com.sparta.newspeed.domain.newsfeed.data;

import com.sparta.newspeed.domain.newsfeed.entity.Ott;
import com.sparta.newspeed.domain.newsfeed.repository.OttRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class OttDataRunner implements CommandLineRunner {

    @Autowired
    private OttRepository ottRepository;

    @Override
    public void run(String... args) throws Exception {
        if (ottRepository.count() == 0) {
            ottRepository.save(new Ott("Netflix", 17000, 4));
            ottRepository.save(new Ott("Disney+", 13900, 4));
            ottRepository.save(new Ott("watcha", 12900, 4));
            ottRepository.save(new Ott("wavve", 13900, 4));
            ottRepository.save(new Ott("tiving", 17000, 4));
        }
    }
}