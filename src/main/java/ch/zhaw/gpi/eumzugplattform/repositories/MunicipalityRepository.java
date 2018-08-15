/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.zhaw.gpi.eumzugplattform.repositories;

import ch.zhaw.gpi.eumzugplattform.entities.MunicipalityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author scep
 */
public interface MunicipalityRepository extends JpaRepository<MunicipalityEntity, Integer> {
        
}
