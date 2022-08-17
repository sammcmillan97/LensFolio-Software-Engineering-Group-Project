package nz.ac.canterbury.seng302.portfolio.repository;

import nz.ac.canterbury.seng302.portfolio.model.PortfolioGroup;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortfolioGroupRepository extends CrudRepository<PortfolioGroup, Integer> {

    PortfolioGroup findByGroupId(int groupId);
}
