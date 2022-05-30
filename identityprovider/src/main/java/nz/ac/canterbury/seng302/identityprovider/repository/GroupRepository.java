package nz.ac.canterbury.seng302.identityprovider.repository;

import nz.ac.canterbury.seng302.identityprovider.entity.Group;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository  extends CrudRepository <Group, Integer> {

    Group findByGroupId(Integer groupId);

    Group findByShortName(String shortName);

    Group findByLongName(String longName);
}
