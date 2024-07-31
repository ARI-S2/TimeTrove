package com.timetrove.Project.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "watch", indexes = {
	    @Index(name = "idx_name", columnList = "name"),
	    @Index(name = "idx_model", columnList = "model")
	})
public class Watch {
	
	@Id
	private Long no;
	
	@Column(unique = true)
	private String name;
	
	private String image;
	private int c_price;
	private int s_price;
	private String points;
	private String discount;
	
	@Column(unique = true)
	private String model;
	
	private int hit;
	
	@Column(name = "dimages")
    private String dimagesString;

    @Transient
    private String[] dimagesArray;

    public void setDimages(String[] dimages) {
        this.dimagesArray = dimages;
        this.dimagesString = dimages.length > 1 ? String.join("|", dimages) : dimages[0];
    }

    public String[] getDimages() {
        if (dimagesArray == null) {
            dimagesArray = dimagesString.contains("|") ? dimagesString.split("\\|") : new String[]{dimagesString};
        }
        return dimagesArray;
    }
    
    // JPA가 데이터 저장 및 수정할 떄 자동실행
    @PrePersist
    @PreUpdate
    private void prePersist() {
        if (dimagesArray != null) {
            dimagesString = dimagesArray.length > 1 ? String.join("|", dimagesArray) : dimagesArray[0];
        }
    }

    @PostLoad
    private void postLoad() {
        dimagesArray = dimagesString.contains("|") ? dimagesString.split("\\|") : new String[]{dimagesString};
    }
}
