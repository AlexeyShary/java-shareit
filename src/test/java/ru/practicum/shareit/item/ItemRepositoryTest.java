package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@Transactional
public class ItemRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void testFindBySearchText() {
        User owner = testEntityManager.persist(User.builder()
                .name("Owner")
                .email("owner@user.com")
                .build());

        testEntityManager.persist(Item.builder()
                .name("Фиолетовый слон")
                .description("Обычный фиолетовый слон")
                .available(true)
                .owner(owner)
                .build());

        testEntityManager.persist(Item.builder()
                .name("Фиолетовый пчел")
                .description("Эпический фиолетовый пчел")
                .available(false)
                .owner(owner)
                .build());

        testEntityManager.persist(Item.builder()
                .name("Красный лягух")
                .description("Легендарный красный лягух")
                .available(true)
                .owner(owner)
                .build());

        testEntityManager.persist(Item.builder()
                .name("Зеленый лягух")
                .description("It's wednesday my dudes")
                .available(true)
                .owner(owner)
                .build());

        Pageable pageable = PageRequest.of(0, 10);

        assertThat(itemRepository.findBySearchText("ТиГр", pageable)).size().isEqualTo(0);
        assertThat(itemRepository.findBySearchText("ФиОлЕтОвЫй", pageable)).size().isEqualTo(1);
        assertThat(itemRepository.findBySearchText("ЛяГуХ", pageable)).size().isEqualTo(2);
    }
}