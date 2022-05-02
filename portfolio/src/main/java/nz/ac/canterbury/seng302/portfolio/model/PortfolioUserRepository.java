package nz.ac.canterbury.seng302.portfolio.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortfolioUserRepository extends CrudRepository<PortfolioUser, Integer> {

    PortfolioUser findById(int id);

}
