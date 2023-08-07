package ru.practicum.shareit.request.model;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * TODO Sprint add-item-requests.
 */

@Getter
@Setter
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Table(name = "requests")
public class ItemRequest {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;
    @EqualsAndHashCode.Include
    @Column(nullable = false, length = 1000)
    private String description;
    @EqualsAndHashCode.Include
    @ManyToOne
    @JoinColumn(name = "requester_id")
    private User requester;
    @EqualsAndHashCode.Include
    @Column
    private LocalDateTime created;
    @Transient
    private Set<Item> items;
}