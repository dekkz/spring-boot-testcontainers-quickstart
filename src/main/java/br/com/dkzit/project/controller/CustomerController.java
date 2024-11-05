package br.com.dkzit.project.controller;

import br.com.dkzit.project.domain.dto.CustomerRequestDTO;
import br.com.dkzit.project.domain.dto.CustomerResponseDTO;
import br.com.dkzit.project.service.CustomerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public List<CustomerResponseDTO> findAll() {
        return customerService.findAll();
        }

    @GetMapping("/{customerId}")
    public CustomerResponseDTO findById(@PathVariable Long customerId) {
        return customerService.findById(customerId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public CustomerResponseDTO save(@RequestBody @Valid CustomerRequestDTO customer) {
        return customerService.save(customer);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{customerId}")
    public CustomerResponseDTO updateCustomer(@PathVariable("customerId") @Min(1) Long customerId, @Valid @RequestBody CustomerRequestDTO customer) {
        return customerService.update(customerId, customer);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{customerId}")
    public void deleteCustomer(@PathVariable("customerId") @Min(1) Long customerId) {
        customerService.delete(customerId);
    }
}
