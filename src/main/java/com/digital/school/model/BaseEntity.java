package com.digital.school.model;

import java.io.Serializable;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;




@MappedSuperclass
public abstract class BaseEntity implements Serializable, Comparable<BaseEntity> {

    private static final long serialVersionUID = -3545803775832286284L;

    /*
	@Id @GeneratedValue(generator="auto-increment")
	@GenericGenerator(name="auto-increment", strategy = "increment")
	@Column(name="ID")
	Long id;
	*/

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQUENCE_WAREHOUSE")
    @SequenceGenerator(name = "SEQUENCE_WAREHOUSE", sequenceName = "hibernate_seq", allocationSize = 1)
    @Column(name="ID")
    Long id;


    /*	@Version
        @Column(name = "VERSION")
        private Integer version;
    */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    /*
        public Integer getVersion() {
            return version;
        }

        public void setVersion(Integer version) {
            this.version = version;
        }
    */
    @Override
    public int hashCode() {
        if (getId() == null)
            return super.hashCode();
        return getId().hashCode();
    }

    /**
     * Implémentation par défaut de l'equal
     *
     * Attention ! : ne pas oublier de surcharger cette méthode pour prendre en
     * compte une business key lorsque l'on ajoute par exemple des instances non
     * persistentes à une Set
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;

        final BaseEntity other = (BaseEntity) obj;
        if (getId() == null) {
            if (other.getId() != null)
                return false;
        } else if (!getId().equals(other.id))
            return false;
        return true;
    }

    public int compareTo(BaseEntity other) {
        if (other == null)
            return 1;
        return (int) (getId() - other.getId());
    }



}
