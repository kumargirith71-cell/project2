package com.example.Profenaa_touch.Repository;

import com.example.Profenaa_touch.entity.SubModule;
import com.example.Profenaa_touch.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubModuleRepository extends JpaRepository<SubModule, Long> {

    List<SubModule> findByModule(Module module);

}
