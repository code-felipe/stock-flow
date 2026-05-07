package com.stockflow.backend.order.enumerate;

public enum OrderStatus {
	
	CONFIRMED,
	SHIPPED,
	DELIVERED,
	CANCELLED;
	
	public boolean canTransitionTo(OrderStatus next) {
        switch (this) {
            case CONFIRMED:
                return next == SHIPPED || next == CANCELLED;
            case SHIPPED:
                return next == DELIVERED;
            default: 
                return false;
        }
    }
}
