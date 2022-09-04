package nz.ac.canterbury.seng302.portfolio.repository.group;

import nz.ac.canterbury.seng302.portfolio.model.group.GroupRepositorySettings;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Simple repository which stores PortfolioGroups
 */
@Repository
public interface GroupRepositorySettingsRepository extends CrudRepository<GroupRepositorySettings, Integer> {

    GroupRepositorySettings findByGroupId(int groupId);
    boolean existsByGroupId(int groupId);

}
