package sn.sonatel.dsi.ins.imoc.dto;



public record ServiceWithDepartmentDTO(
    Long id,
     String name,
     String description,
     Long departmentId,
     String departmentName
) {
    @Override
    public Long id() {
        return id;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public Long departmentId() {
        return departmentId;
    }

    @Override
    public String departmentName() {
        return departmentName;
    }
}
