package nl.tmichels.tcxtojson.tcdbtosimplifiedmodel;

import lombok.extern.slf4j.Slf4j;

import javax.xml.datatype.XMLGregorianCalendar;

@Slf4j
public class CadenceConflictResolver {

    static Short getCadence(Short coreCadence, Short extensionCadence, XMLGregorianCalendar xmlGregorianCalendar) {
        if (extensionCadence != null && coreCadence != null && !extensionCadence.equals(coreCadence)) {
            log.warn("Conflicting values for cadence in trackpoint or lap on {}: extension: {}, core value: {}",
                    xmlGregorianCalendar,
                    extensionCadence,
                    coreCadence);
        }

        if (coreCadence == null && extensionCadence != null) {
            return extensionCadence;
        } else {
            return coreCadence;
        }
    }

}
