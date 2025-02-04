package com.github.dangelcrack.model.dao;

import org.hibernate.Session;
@FunctionalInterface
public interface TransaccionOperacion<T> {
    T ejecutar(Session session);
}
