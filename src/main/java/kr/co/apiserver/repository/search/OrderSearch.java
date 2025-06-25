package kr.co.apiserver.repository.search;

import kr.co.apiserver.dto.OrderDetailResponseDto;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderSearch {
    OrderDetailResponseDto findOrderDetailByOno(Long ono);
}
