package nz.ac.canterbury.seng302.portfolio.repository.user;

import nz.ac.canterbury.seng302.portfolio.model.user.PortfolioUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Simple repository which stores PortfolioUsers
 */
@Repository
public interface PortfolioUserRepository extends CrudRepository<PortfolioUser, Integer> {

    PortfolioUser findByUserId(int userId);

}
