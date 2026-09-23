package com.epam.franquicias.usecase;

import com.epam.franquicias.domain.model.Branch;

public interface AddBranchUseCase {
    Branch execute(String franchiseId, String branchName);
}
