package com.pricing.repository;

import com.pricing.rule.PromotionRule;

import java.util.List;

// Repository interface — abstracts rule loading from the engine
public interface RuleRepository {
    List<PromotionRule> loadAll();
}
