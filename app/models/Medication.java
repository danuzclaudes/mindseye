package models;


import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="medication")
public class Medication extends Model{
    @Id
    @Column(name="med_id")
    public int medId;
    @Column(name="med_name", columnDefinition = "varchar(32)")
    public String medName;
    // start of current drug should be found by most recent start date
    @Column(name="med_start_date", columnDefinition = "date not null")
    public Date medStartDate;
    // optional if continually taking drug till today's visit
    @Column(name="med_end_date", columnDefinition = "date")
    public Date medEndDate;
    // group for each medication class for TimeLine
    @Column(name="med_group", columnDefinition = "varchar(5) not null")
    public String medGroup;

    /**
     * The `prescribed_on_visit` field is the foreign key to Table `Visit`:
     * "A DRUG CAN BE PRESCRIBED ONLY ON ONE VISIT"
     * Note that the field must be `Visit` type object as foreign key.
     */
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name= "prescribed_on_visit", referencedColumnName = "visit_id")
    public Visit prescribed_on_visit;

    /**
     * Constructor for Medication
     * @param name
     * @param start
     * @param end
     * @param group
     * @param visitId
     */
    public Medication(String name, Date start, Date end, String group, String visitId){
        this.medName = name;
        this.medStartDate = start;
        this.medEndDate = end;
        this.medGroup = group;
        this.prescribed_on_visit = Visit.find.byId(visitId);
    }

    /**
     * The `find` field will be used to programatically make queries
     */
    public static Finder<Long, Medication> find = new Finder<Long, Medication>(
        Long.class, Medication.class
    );

    /**
     * Create a medication/drug/therapy programatically.
     * A new medicine will be saved into database.
     * @param name
     * @param start
     * @param end
     * @param group
     * @param visitId
     * @return
     */
    public static Medication create(String name, Date start,
                                    Date end, String group, String visitId){
        Medication med = new Medication(name, start, end, group, visitId);
        med.save();
        return med;
    }

    /**
     * Retrieve all drugs/medications prescribed through visit (id `visit`).
     * @param   visit   a MD5-hashed visit id generated by current timestamp
     * @return  List<Medication>    a list of all medicines related with this visit
     */
    public static List<Medication> findMedsByVisit(String visit){
        return find.where().eq("prescribed_on_visit", Visit.find.ref(visit)).findList();
    }
}
