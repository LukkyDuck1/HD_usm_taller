package cl.usm.sansaweigh.services;

import cl.usm.sansaweigh.entities.RegistroPesaje;
import cl.usm.sansaweigh.repositories.RegistroPesajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RegistroPesajeServiceImpl implements RegistroPesajeService {

    @Autowired
    RegistroPesajeRepository registroPesajeRepository;

    @Override
    public RegistroPesaje create(RegistroPesaje registro) {
        //TODO: conversion a Sansas, clasificacion, restricciones de balanza, estado inicial INGRESADO
        return this.registroPesajeRepository.insert(registro);
    }

    @Override
    public RegistroPesaje update(String id, RegistroPesaje registro) {
        //TODO: validar maquina de estados (IllegalWeighingStateException), actualizar updatedAt
        return this.registroPesajeRepository.save(registro);
    }

    @Override
    public List<RegistroPesaje> getByDate(LocalDate fecha) {
        //TODO: filtrar por fecha usando createdAt entre inicio y fin del dia
        return this.registroPesajeRepository.findByCreatedAtBetween(
                fecha.atStartOfDay(), fecha.atTime(23, 59, 59));
    }
}
