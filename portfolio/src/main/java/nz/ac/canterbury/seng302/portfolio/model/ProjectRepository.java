package nz.ac.canterbury.seng302.portfolio.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// the entity type and ID this works with are specified in the signature
@Repository
public interface ProjectRepository extends CrudRepository<Project, Integer> {

    // you can create queries using method names
    // start with "findBy", and then put one or more names (and they MUST match the java class file names!)
    // you can join multiple attributes with and, e.g. findByProjectNameAndId for attributes ProjectName, Id
    List<Project> findByProjectName(String projectName);

    Project findById(int id);
}
