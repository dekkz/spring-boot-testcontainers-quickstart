package br.com.dkzit.project.service;

import br.com.dkzit.project.domain.dto.CustomerRequestDTO;
import br.com.dkzit.project.domain.dto.CustomerResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomerService {

    CustomerResponseDTO findById(Long id);
    List<CustomerResponseDTO> findAll();

    Page<CustomerResponseDTO> findAll(Pageable pageable);
    CustomerResponseDTO save(CustomerRequestDTO customer);
    CustomerResponseDTO update(Long id, CustomerRequestDTO customer);

    void delete(Long id);
}
