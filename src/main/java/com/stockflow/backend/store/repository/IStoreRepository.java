package com.stockflow.backend.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.stockflow.backend.store.domain.Store;

public interface IStoreRepository extends JpaRepository<Store, Long>, JpaSpecificationExecutor<Store> {

}
