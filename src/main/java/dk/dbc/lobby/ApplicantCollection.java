/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt or at https://opensource.dbc.dk/licenses/gpl-3.0/
 */

package dk.dbc.lobby;

import java.util.List;

public class ApplicantCollection {

    private List<Applicant> applicants;

    public List<Applicant> getApplicants() {
        return applicants;
    }

    public void setApplicants(List<Applicant> applicants) {
        this.applicants = applicants;
    }

    Applicant[] toArray() {
        return applicants.stream()
                .sorted()
                .toArray(Applicant[]::new);
    }
}
