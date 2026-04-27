package com.stockflow.backend.order.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.stockflow.backend.order.domain.Order;

public interface IOrderRespository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order>{
	
	public Page<Order> findByStoreId(Long storeId, Pageable page);
	
	@Query("SELECT o FROM Order o WHERE o.id = :orderId AND o.store.id = :storeId")
	Optional<Order> findByIdAndStoreId(@Param("orderId") Long orderId, @Param("storeId") Long storeId);

}
