package nz.ac.canterbury.seng302.portfolio.repository.group;

import nz.ac.canterbury.seng302.portfolio.model.group.PortfolioGroup;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioGroupRepository extends CrudRepository<PortfolioGroup, Integer> {

    void deleteByGroupId(int groupId);
    Optional<PortfolioGroup> findByGroupId(int groupId);

    List<PortfolioGroup> findByParentProjectId(int parentProjectId);

    boolean existsByGroupId(int groupId);
}
