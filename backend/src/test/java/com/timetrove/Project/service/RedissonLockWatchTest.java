package com.timetrove.Project.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

//@SpringBootTest
public class RedissonLockWatchTest {
/*    private static final Logger log = LoggerFactory.getLogger(PessimisticLockWatchTest.class);
    @Autowired
    private WatchRepository watchRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedissonLockWatchFacade redissonLockWatchFacade;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    private static final int USER_COUNT = 100;
    private static final long INITIAL_QUANTITY = 100L;
    private static final long WATCH_ID = 1000L;


    @BeforeEach
    public void setup() {
        // 테스트를 위한 초기 데이터 설정
        Watch watch = new Watch();
        watch.setId(WATCH_ID);
        watch.setQuantity(INITIAL_QUANTITY);
        watch = watchRepository.saveAndFlush(watch);

        for (int i = 0; i < USER_COUNT; i++) {
            User user = new User((long) i, "img", "nickname" + i, "ROLE_USER");
            userRepository.saveAndFlush(user);

            Cart cart = new Cart(user, watch, 1L, false, null);
            cartRepository.saveAndFlush(cart);
        }
    }

//    @AfterEach
//    @Transactional
//    public void tearDown() {
//        // 테스트 후 데이터 정리 쿼리 실행
//        jdbcTemplate.execute("DELETE FROM cart WHERE id > 16");
//        jdbcTemplate.execute("DELETE FROM watch WHERE watch_id > 450");
//        jdbcTemplate.execute("DELETE FROM user_timetrove WHERE user_code < 200");
//    }


    @Test
    public void PurchaseConcurrencyTest() throws InterruptedException {
        //멀티스레드 이용 ExecutorService : 비동기 작업을 단순하게 처리할 수 있도록 해주는 java api
        ExecutorService executorService = Executors.newFixedThreadPool(USER_COUNT);
        //다른 스레드에서 수행이 완료될 때 까지 대기할 수 있도록 도와주는 API - 요청이 끝날때 까지 기다림
        CountDownLatch latch = new CountDownLatch(USER_COUNT);

        for (int i = 0; i < USER_COUNT; i++) {
            long userCode = i;
            executorService.submit(() -> {
                try {
                    List<CartDto> cartDtoList = cartRepository.findByUser_UserCodeAndPurchasedFalse(userCode)
                            .stream()
                            .map(CartDto::convertCartToDto2)
                            .toList();

                    redissonLockWatchFacade.processPurchase(cartDtoList);

                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 스레드가 작업을 마칠 때까지 대기

        // 결과 확인
        Watch updatedWatch = watchRepository.findById(WATCH_ID).orElseThrow();
        assertEquals(INITIAL_QUANTITY - USER_COUNT, updatedWatch.getQuantity(),
                "재고가 정확하게 감소해야 합니다.");

    }*/
}