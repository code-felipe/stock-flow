package com.stockflow.backend.dashboard.repository;

import org.springframework.data.domain.Pageable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.stockflow.backend.dashboard.dto.LowStockProductDTO;
import com.stockflow.backend.dashboard.dto.OrdersByMonthDTO;
import com.stockflow.backend.order.domain.Order;
import com.stockflow.backend.order.enumerate.OrderStatus;

public interface IDashboardRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
	
	@Query("""
	        SELECT new com.stockflow.backend.dashboard.dto.OrdersByMonthDTO(
	            YEAR(o.orderDate),
	            MONTH(o.orderDate),
	            COUNT(o),
	            SUM(o.total)
	        )
	        FROM Order o
	        WHERE YEAR(o.orderDate) = :year
	        GROUP BY YEAR(o.orderDate), MONTH(o.orderDate)
	        ORDER BY YEAR(o.orderDate), MONTH(o.orderDate)
	        """)
	List<OrdersByMonthDTO> countOrdersByMonth(
		    @Param("year") int year
		);
	
	@Query("""
		    SELECT AVG(o.total)
		    FROM Order o
		    WHERE YEAR(o.orderDate) = :year
		    AND o.orderStatus = :status
		""")
		Double getAverageOrderValue(
		    @Param("year") int year,
		    @Param("status") OrderStatus status
		);
	
	@Query("""
		    SELECT COALESCE(SUM(oi.quantity), 0)
		    FROM Order o
		    JOIN o.orderItems oi
		    WHERE YEAR(o.orderDate) = :year
		      AND o.orderStatus = :status
		""")
		Long getProductsSold(
		    @Param("year") int year,
		    @Param("status") OrderStatus status
		);
	
	@Query("""
		    SELECT new com.stockflow.backend.dashboard.dto.LowStockProductDTO(
		        i.product.name,
		        i.onHand,
		        i.minStock
		    )
		    FROM Inventory i
		    WHERE i.onHand <= i.minStock
		    ORDER BY i.onHand ASC
		""")
		List<LowStockProductDTO> findLowStockProducts(Pageable pageable);

}
