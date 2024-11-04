package com.timetrove.Project.service;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


//@Component
// 낙관적 락 사용 시 업데이트를 실패한 경우 재시도를 위한 객체
public class OptimisticLockWatchFacade {

/*    private final MypageService mypageService;

    public OptimisticLockWatchFacade(MypageService mypageService) {
        this.mypageService = mypageService;
    }

    @Transactional
    public void updateInventory(Long userCode, List<Long> cartIds) throws InterruptedException {
        while (true) {
            try {
                mypageService.purchaseItems(userCode, cartIds);
                break; // 재고 차감에 성공하면 while 루프 종료
            } catch (ObjectOptimisticLockingFailureException e) {
                // 낙관적 락 충돌 발생 시 50ms 대기 후 재시도
                //log.error("{} 발생, 업데이트 실패", e.getClass().getSimpleName(), e);
                Thread.sleep(50);
            }
        }
    }*/
}
