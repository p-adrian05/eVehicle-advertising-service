package org.example;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class WebMain  {

//    private final UserRepository userRepository;
//    private final RoleRepository roleRepository;
//    private final AdvertisementRepository advertisementRepository;
//    private final UserDaoImpl userDao;
//    private final MessageDao messageDao;
//    private final AdvertisementDaoImpl advertisementDao;
//    private final ImageRepository adImageRepository;
//    private final UserMessageRepository userMessageRepository;
//    private final UserRateRepository userRateRepository;
//    private final UserRateDao userRateDao;
//    private final MessageService messageService;


    public static void main(String[] args) {
          SpringApplication.run(WebMain.class,args);
        //  }
//        roleRepository.save(role);
            // RoleEntity role = roleRepository.findById(1).get();
//      UserEntity user = UserEntity.builder().activation(" ")
//               .created(new Timestamp(new Date().getTime()))
//               .id(0)
//               .email(" email")
//               .enabled(true)
//               .lastLogin(new Timestamp(new Date().getTime()))
//               .password("ass")
//               .username("uiser")
//              .roles(new HashSet<>())
//               .build();
//       user.addRole(role);
            //UserEntity userEntity = userRepository.findById(2).get();
//        user.setEmail("emailtest");
//        user.setSavedAds(new HashSet<>());
//        AdvertisementEntity advertisementEntity = advertisementRepository.findById(2).get();
//        user.addSavedAd(advertisementEntity);
//       userRepository.save(user);
//
//        Advertisement advertisement = Advertisement.builder()
//                .category("Car")
//                .creator("adrian")
//                .description("desc")
//                .type("Model C")
//                .brand("DS")
//                .title("New Tesla")
//                .images(List.of(
//                        AdImage.builder().imgName("asdsa").extension("JPG").location("loc").build(),
//                        AdImage.builder().imgName("asdsa1").extension("JPG").location("loc").build(),
//                        AdImage.builder().imgName("asdsa2").extension("JPG").location("loc").build()
//                ))
//                .price(12121212)
//                .build();
//
//        advertisementDao.createAdvertisement(advertisement);
            //advertisementDao.deleteAdvertisement(13);

//        ProductDetails productDetails= ProductDetails.builder()
//                .adId(5)
//                .accelaration(5.5)
//                .year(2000)
//                .batterySize(121)
//                .chargeSpeed(1212)
//                .hp(1212)
//                .km(1212)
//                .drive(Drive.AWD)
//                .maxSpeed(222)
//                .range(222)
//                .state(ProductState.SPARED)
//                .weight(12121)
//                .build();
//        advertisementDao.updateProductDetails(productDetails);
            //advertisementDao.getAdvertisementsByCategory("Car").forEach(System.out::println);
            //advertisementDao.getAdImagesByAdvertisementId(1).forEach(System.out::println);
            // advertisementDao.getSavedAdvertisementsByUsername("user1").forEach(System.out::println);
            //System.out.println(adImageRepository.getFirstByAdvertisementEntity_Id(1));
            // System.out.println(advertisementDao.getProductDetailsById(1));
//        Advertisement advertisement = advertisementDao.getAdvertisementById(1);
//        advertisement.getImages().add(AdImage.builder().imgName("asdsa4124").extension("JPG").location("loc").build());
//        advertisement.setType("Model 45");
//       advertisementDao.updateAdvertisement(advertisement);

            //userMessageRepository.findByReceiverUser_UsernameAndSenderUser_UsernameOrReceiverUser_UsernameAndSenderUser_Username(2,3).forEach(System.out::println);
            //serMessageRepository.findUserMessageEntitiesBySenderUser_UsernameAndOrReceiverUser_Username("user3","user2","user2","user3").forEach(System.out::println);
            //messageDao.createMessage(message);
//        Message message = Message.builder()
//                .senderUserName("user5")
//                .receiverUsernames(List.of("user2"))
//                .content("Message contetnadkl;fjhaskljgh faskjl ghkasdjlf ghjkdfs ")
//                .build();
//        messageDao.createMessage(message);
            //Message message = messageDao.getMessagesByUsernames("user1","user5").stream().findFirst().get();
//     message.setContent("updated content");
//     messageDao.updateMessage(message);
            //System.out.println(userMessageRepository.countUserMessageEntityByMessage_Id(8));
            //System.out.println(userMessageRepository.countDistinctByUnreadIsFalseAndMessage_Id(5));
            // messageDao.deleteMessage(message);

            //userRateRepository.findByRatedUser_UsernameAndStateOrderByRate("user1", UserRateState.BUYER).forEach(System.out::println);
            //userRateRepository.findByRatedUser_UsernameAndStateOrderByRate("user2", UserRateState.SELLER).forEach(System.out::println);
//        long startTime = System.currentTimeMillis();
//        userRateDao.getRatesByUsername("user1",UserRateState.BUYER);
//        long endTime = System.currentTimeMillis();
//        System.out.println("That took " + (endTime - startTime) + " milliseconds");
//        UserRate userRate = UserRate.builder()
//                .id(8)
//                .rateState(RateState.NEGATIVE.toString())
//                .advertisement(Advertisement.builder()
//                .title("Tesla model Y")
//                .id(3)
//                .state(AdState.ARCHIVED)
//                .build())
//                .description("Negative description rate")
//                .ratingUsername("user2")
//                .ratedUsername("user1")
//                .ratedState(UserRateState.SELLER.toString())
//                .build();
//        userRateDao.createUserRate(userRate);
            // userRateDao.deleteUserRate(7);
            //System.out.println(userRateDao.getRatesCountByUsernameAndRateState("user1",RateState.POSITIVE));
            //System.out.println(userRateDao.getRatesCountByUsernameAndRateState("user1",RateState.NEGATIVE));

            //System.out.println(userDao.getUserByName("user1"));

            //messageDao.getConversationPartnersUsername("user3").forEach(System.out::println);
            //System.out.println(userMessageRepository.existsDistinctByReceiverUser_UsernameAndSenderUser_UsernameAndUnreadIsTrue("user1","user3"));
            //System.out.println(userMessageRepository.countByReceiverUserUsernameAndUnreadIsTrue("user2"));
            //System.out.println(messageService.getConversationUsernames());

        }




    //@Override
    //public void run(String... args) throws Exception {
        //userDao.addRole("user2", "ADMIN");
        //userDao.addSaveAd("user1",2);
   // }
}