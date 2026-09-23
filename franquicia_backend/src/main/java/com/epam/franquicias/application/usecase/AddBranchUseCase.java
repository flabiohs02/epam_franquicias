package com.epam.franquicias.application.usecase;

import com.epam.franquicias.domain.model.Branch;

public interface AddBranchUseCase {
    Branch execute(String franchiseId, String branchName);
}
