package com.lab.config;

import com.lab.entity.*;
import com.lab.entity.enums.*;
import com.lab.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private LabRepository labRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChemicalRepository chemicalRepository;

    @Override
    public void run(String... args) {
        if (labRepository.findAll().isEmpty()) {
            initLabs();
            initUsers();
            initChemicals();
        }
    }

    private void initLabs() {
        Lab lab1 = new Lab();
        lab1.setName("化学分析实验室-A101");
        lab1.setCode("LAB-A101");
        lab1.setSafetyLevel(DangerLevel.HIGH);
        lab1.setStorageCapacity(1000.0);
        lab1.setUsedCapacity(0.0);
        lab1.setAvailableConditions(Arrays.asList(
                StorageCondition.NORMAL,
                StorageCondition.REFRIGERATED,
                StorageCondition.VENTILATED
        ));
        lab1.setCanReceive(true);
        labRepository.save(lab1);

        Lab lab2 = new Lab();
        lab2.setName("生物实验室-B202");
        lab2.setCode("LAB-B202");
        lab2.setSafetyLevel(DangerLevel.MEDIUM);
        lab2.setStorageCapacity(500.0);
        lab2.setUsedCapacity(0.0);
        lab2.setAvailableConditions(Arrays.asList(
                StorageCondition.NORMAL,
                StorageCondition.REFRIGERATED
        ));
        lab2.setCanReceive(true);
        labRepository.save(lab2);

        Lab lab3 = new Lab();
        lab3.setName("物理实验室-C303");
        lab3.setCode("LAB-C303");
        lab3.setSafetyLevel(DangerLevel.LOW);
        lab3.setStorageCapacity(300.0);
        lab3.setUsedCapacity(0.0);
        lab3.setAvailableConditions(Arrays.asList(
                StorageCondition.NORMAL,
                StorageCondition.DARK
        ));
        lab3.setCanReceive(true);
        labRepository.save(lab3);
    }

    private void initUsers() {
        User admin = new User();
        admin.setName("系统管理员");
        admin.setEmployeeId("ADMIN001");
        admin.setStatus(UserStatus.ACTIVE);
        admin.setQualifications(Arrays.asList("危化品管理资质", "安全管理员资质"));
        admin.setAllowedCategories(Arrays.asList("ALL"));
        admin.setSingleLimit(100.0);
        userRepository.save(admin);

        User user1 = new User();
        user1.setName("张研究员");
        user1.setEmployeeId("USER001");
        user1.setLabId(labRepository.findAll().get(0).getId());
        user1.setStatus(UserStatus.ACTIVE);
        user1.setQualifications(Arrays.asList("化学试剂使用资质", "实验室安全培训"));
        user1.setAllowedCategories(Arrays.asList("酸类", "碱类", "有机溶剂"));
        user1.setSingleLimit(10.0);
        userRepository.save(user1);

        User user2 = new User();
        user2.setName("李工程师");
        user2.setEmployeeId("USER002");
        user2.setLabId(labRepository.findAll().get(1).getId());
        user2.setStatus(UserStatus.ACTIVE);
        user2.setQualifications(Arrays.asList("生物试剂使用资质", "应急处理资质"));
        user2.setAllowedCategories(Arrays.asList("生物试剂", "消毒剂"));
        user2.setSingleLimit(5.0);
        userRepository.save(user2);

        User user3 = new User();
        user3.setName("王博士");
        user3.setEmployeeId("USER003");
        user3.setLabId(labRepository.findAll().get(2).getId());
        user3.setStatus(UserStatus.ACTIVE);
        user3.setQualifications(Arrays.asList("物理试剂使用资质", "辐射防护资质"));
        user3.setAllowedCategories(Arrays.asList("金属类", "盐类"));
        user3.setSingleLimit(20.0);
        userRepository.save(user3);
    }

    private void initChemicals() {
        Chemical chem1 = new Chemical();
        chem1.setName("硫酸");
        chem1.setCasNumber("7664-93-9");
        chem1.setDangerLevel(DangerLevel.HIGH);
        chem1.setStorageCondition(StorageCondition.VENTILATED);
        chem1.setMaxStock(100.0);
        chem1.setCurrentStock(50.0);
        chem1.setExpiryDate(LocalDate.now().plusYears(2));
        chem1.setUnit("L");
        chem1.setDescription("强酸，具有强腐蚀性");
        chemicalRepository.save(chem1);

        Chemical chem2 = new Chemical();
        chem2.setName("氢氧化钠");
        chem2.setCasNumber("1310-73-2");
        chem2.setDangerLevel(DangerLevel.MEDIUM);
        chem2.setStorageCondition(StorageCondition.DRY);
        chem2.setMaxStock(200.0);
        chem2.setCurrentStock(100.0);
        chem2.setExpiryDate(LocalDate.now().plusYears(3));
        chem2.setUnit("kg");
        chem2.setDescription("强碱，具有腐蚀性");
        chemicalRepository.save(chem2);

        Chemical chem3 = new Chemical();
        chem3.setName("乙醇");
        chem3.setCasNumber("64-17-5");
        chem3.setDangerLevel(DangerLevel.MEDIUM);
        chem3.setStorageCondition(StorageCondition.VENTILATED);
        chem3.setMaxStock(500.0);
        chem3.setCurrentStock(300.0);
        chem3.setExpiryDate(LocalDate.now().plusYears(1));
        chem3.setUnit("L");
        chem3.setDescription("有机溶剂，易燃");
        chemicalRepository.save(chem3);

        Chemical chem4 = new Chemical();
        chem4.setName("氰化钾");
        chem4.setCasNumber("151-50-8");
        chem4.setDangerLevel(DangerLevel.EXTREME);
        chem4.setStorageCondition(StorageCondition.DARK);
        chem4.setMaxStock(10.0);
        chem4.setCurrentStock(5.0);
        chem4.setExpiryDate(LocalDate.now().plusYears(5));
        chem4.setUnit("kg");
        chem4.setDescription("剧毒化学品，需双人管理");
        chemicalRepository.save(chem4);

        Chemical chem5 = new Chemical();
        chem5.setName("氯化钠");
        chem5.setCasNumber("7647-14-5");
        chem5.setDangerLevel(DangerLevel.LOW);
        chem5.setStorageCondition(StorageCondition.NORMAL);
        chem5.setMaxStock(1000.0);
        chem5.setCurrentStock(500.0);
        chem5.setExpiryDate(LocalDate.now().plusYears(10));
        chem5.setUnit("kg");
        chem5.setDescription("普通盐类，低危险性");
        chemicalRepository.save(chem5);
    }
}
