package borme.services;

import borme.domain.ConstitucionEmpresa;
import borme.repository.ConstitucionEmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ConstitucionEmpresaService {
    @Autowired
    private ConstitucionEmpresaRepository repository;

    public List<ConstitucionEmpresa> buscarPorNombre(String nombre) {
        return repository.findByNombreEmpresaContaining(nombre);
    }

    public ConstitucionEmpresa guardar(ConstitucionEmpresa empresa) {
        return repository.save(empresa);
    }
}


