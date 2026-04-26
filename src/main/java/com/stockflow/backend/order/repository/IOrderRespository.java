package com.stockflow.backend.order.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.stockflow.backend.order.domain.Order;

public interface IOrderRespository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order>{
	
	public Page<Order> findByStoreId(Long storeId, Pageable page);

}
