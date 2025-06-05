//package com.kasa.adr.service.scheduler;
//
//import com.kasa.adr.model.Case;
//import com.kasa.adr.model.CaseHistory;
//import com.kasa.adr.repo.CaseRepository;
//import com.kasa.adr.service.VenkyNotificationService;
//import org.checkerframework.checker.units.qual.A;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.text.SimpleDateFormat;
//import java.time.Instant;
//import java.util.Date;
//import java.util.List;
//
/// /@Component
//public class NotificationScheduler {
//
//
//    private  final Logger log = LoggerFactory.getLogger(NotificationScheduler.class);
//
//    private  final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
//
//    @Autowired
//    VenkyNotificationService venkyNotificationService;
//    @Autowired
//    CaseRepository caseRepository;
//
//    //@Scheduled(cron = "0 0 10 * * ?")
//    public void testNotification() {
//        log.info("The time is now {}", dateFormat.format(new Date()));
//        List<Case> cases = caseRepository.findAllByUploadId("6808e63e05f0514ed6e4937c");
//        cases.stream().forEach(aCase -> {
//            String text = aCase.getLoanRecallNoticeNumber();
//            log.info(text);
//            String updatedText = text.replace("COMM", "ARB1");
//            aCase.setInvocationRefNo(updatedText);
//            aCase.getHistory().add(CaseHistory.builder().date(Instant.now()).descriptions("Second Arbitration Notice Sent").build());
//        });
//        caseRepository.saveAll(cases);
//       venkyNotificationService.sendNotice(cases,"second");
//    }
//
//   // @Scheduled(cron = "0 0 11 * * ?")
//    public void actualNotification() {
//        log.info("The time is now {}", dateFormat.format(new Date()));
//        List<Case> cases = caseRepository.findAllByUploadId("6808ebfd05f0514ed6e49380");
//        cases.stream().forEach(aCase -> {
//            String text = aCase.getLoanRecallNoticeNumber();
//            log.info(text);
//            String updatedText = text.replace("COMM", "ARB1");
//            aCase.setInvocationRefNo(updatedText);
//            aCase.getHistory().add(CaseHistory.builder().date(Instant.now()).descriptions("Second Arbitration Notice Sent").build());
//        });
//        //caseRepository.saveAll(cases);
//        //venkyNotificationService.sendNotice(cases,"second");
//    }
//}
