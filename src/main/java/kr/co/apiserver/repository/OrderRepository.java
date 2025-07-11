package kr.co.apiserver.repository;

import kr.co.apiserver.domain.Orders;
import kr.co.apiserver.repository.search.OrderSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface OrderRepository extends JpaRepository<Orders, Long>, OrderSearch {

}
