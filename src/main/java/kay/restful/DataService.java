package kay.restful;

import kay.restful.domain.AppGF;

import java.util.List;

public interface DataService {
    List<Object> getTable(String table);
    List<AppGF> getAppGF();
    String deleteAllAppGF();
    String insertAppGF(List<AppGF> appGFList);

}
