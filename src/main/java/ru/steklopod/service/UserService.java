package ru.steklopod.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.steklopod.Starter;
import ru.steklopod.entities.mariadb.UserRebased;
import ru.steklopod.repositories.maria.UserOutDAO;

@Service
public class UserService {
    @Autowired
    private UserOutDAO userOutDAO;

//    @Transactional(rollbackFor = PersistenceException.class)
    public void saveUser(UserRebased userRebased) {
        try {
            userOutDAO.save(userRebased);
            Starter.savedIds.put(userRebased.getCustomerId(), userRebased.getId());


            if (Checker.isNotConfirmedEmailWithBets(userRebased)) {
                Checker.notConfirmedEmailWithBets.getAndIncrement();
            }
        } catch (Throwable e) {
            Starter.countOfUserRollbacks++;
        }
    }
}
