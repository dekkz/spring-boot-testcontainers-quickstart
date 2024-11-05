package br.com.dkzit.project.service;

import br.com.dkzit.project.domain.dto.CustomerRequestDTO;
import br.com.dkzit.project.domain.dto.CustomerResponseDTO;
import br.com.dkzit.project.domain.entity.CustomerEntity;
import br.com.dkzit.project.exception.CustomerNotFoundException;
import br.com.dkzit.project.repository.CustomerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    private final ModelMapper modelMapper;

    public CustomerServiceImpl(CustomerRepository customerRepository, ModelMapper modelMapper) {
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CustomerResponseDTO findById(Long id) {
        CustomerEntity customer = customerRepository.findById(id)
                .orElseThrow(CustomerNotFoundException::new);

        return modelMapper.map(customer, CustomerResponseDTO.class);
    }

    @Override
    public List<CustomerResponseDTO> findAll() {
        List<CustomerEntity> customers = customerRepository.findAll();
        return customers.stream()
                .map(customer -> modelMapper.map(customer, CustomerResponseDTO.class))
                .toList();
    }

    @Override
    public Page<CustomerResponseDTO> findAll(Pageable pageable) {
        return customerRepository.findAll(Pageable.ofSize(pageable.getPageSize()).withPage(pageable.getPageNumber()))
                .map(customer -> modelMapper.map(customer, CustomerResponseDTO.class));
    }

    @Override
    public CustomerResponseDTO save(CustomerRequestDTO customer) {
        CustomerEntity customerEntity = modelMapper.map(customer, CustomerEntity.class);
        customerEntity = customerRepository.save(customerEntity);
        return modelMapper.map(customerEntity, CustomerResponseDTO.class);
    }

    @Override
    public CustomerResponseDTO update(Long id, CustomerRequestDTO customer) {
        CustomerEntity existingCustomer = customerRepository.findById(id)
                .orElseThrow(CustomerNotFoundException::new);

        existingCustomer.setFirstName(customer.getFirstName());
        existingCustomer.setLastName(customer.getLastName());

        CustomerEntity updatedCustomer = customerRepository.save(existingCustomer);
        return modelMapper.map(updatedCustomer, CustomerResponseDTO.class);
    }

    @Override
    public void delete(Long id) {
        CustomerEntity existingCustomer = customerRepository.findById(id)
                .orElseThrow(CustomerNotFoundException::new);
        customerRepository.delete(existingCustomer);
    }
}
