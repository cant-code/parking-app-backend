package com.mini.ecommerceapp.services.implementations;

import com.mini.ecommerceapp.exceptions.ResourceNotFoundException;
import com.mini.ecommerceapp.models.VehicularSpace;
import com.mini.ecommerceapp.repository.VehicularSpaceRepository;
import com.mini.ecommerceapp.services.VehicularSpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.mini.ecommerceapp.utils.Constants.VEHICLE_SPACE_NOT_FOUND;

@Service
@Transactional
public class VehicularSpaceServiceImpl implements VehicularSpaceService {
    private final VehicularSpaceRepository vehicularSpaceRepository;

    @Autowired
    public VehicularSpaceServiceImpl(VehicularSpaceRepository vehicularSpaceRepository) {
        this.vehicularSpaceRepository = vehicularSpaceRepository;
    }

    @Override
    public List<VehicularSpace> getAllVehicularSpace() {
        return vehicularSpaceRepository.findAll();
    }

    @Override
    public VehicularSpace getVehicularSpace(String s, String name) {
        return vehicularSpaceRepository.getByNameAndParkingSpace_Name(name, s).orElseThrow(() -> new ResourceNotFoundException(VEHICLE_SPACE_NOT_FOUND));
    }

    @Override
    public VehicularSpace getVehicularSpace(long id) {
        return vehicularSpaceRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(VEHICLE_SPACE_NOT_FOUND));
    }

    @Override
    public VehicularSpace saveVehicularSpace(VehicularSpace vehicularSpace) {
        return vehicularSpaceRepository.save(vehicularSpace);
    }

    @Override
    public VehicularSpace updateVehicularSpace(VehicularSpace vehicularSpace) {
        checkID(vehicularSpace.getId());
        return vehicularSpaceRepository.save(vehicularSpace);
    }

    @Override
    public void deleteVehicularSpace(long id) {
        checkID(id);
        vehicularSpaceRepository.deleteById(id);
    }

    private void checkID(long id) {
        if (!vehicularSpaceRepository.existsById(id)) {
            throw new ResourceNotFoundException(VEHICLE_SPACE_NOT_FOUND);
        }
    }
}
