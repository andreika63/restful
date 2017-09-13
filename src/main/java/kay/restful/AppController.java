package kay.restful;

import kay.restful.domain.AppGF;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class AppController {
    private DataService service;

    public AppController(DataService service) {
        this.service = service;
    }

    @RequestMapping(value = "table", method = RequestMethod.GET)
    private List<Object> getTable(@RequestParam(value="name") String table) {
        return service.getTable(table);
    }

    @RequestMapping(value = "appgf/select", method = RequestMethod.GET)
    private List<AppGF> getAppGF() {
        return service.getAppGF();
    }

    @RequestMapping(value = "appgf/delete", method = RequestMethod.DELETE)
    private ResponseEntity<String> deleteAllAppGF() {
        return new ResponseEntity<String>(service.deleteAllAppGF(), HttpStatus.OK);
    }

    @RequestMapping(value = "appgf/insert", method = RequestMethod.POST)
    private ResponseEntity<String> insertAppGF(@RequestBody List<AppGF> appGFList) {
        return new ResponseEntity<String>(service.insertAppGF(appGFList), HttpStatus.OK);
    }


}
