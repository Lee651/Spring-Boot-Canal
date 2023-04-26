package top.rectorlee.entity;

import lombok.*;

import java.io.Serializable;

/**
 * @author Lee
 * @description
 * @date 2023-04-26  10:37:25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements Serializable {
    private Integer id;

    private String name;

    private String nickName;
}
