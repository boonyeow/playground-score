/*
 * Copyright 2021 ICONation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iconloop.score.token.alice;
import com.iconloop.score.util.EnumerableIntMap;
import score.*;
import score.annotation.External;
import score.annotation.Payable;

import java.math.BigInteger;

import static java.math.BigInteger.ONE;

public abstract class IRC31MintBurn extends IRC31Basic {

    // ================================================
    // SCORE DB
    // ================================================
    // id ==> creator
    private final DictDB<BigInteger, Address> creators = Context.newDictDB("creators", Address.class);

    // Mapping from _id to Address
    public final EnumerableIntMap<Address> tokenOwners = new EnumerableIntMap<>("owners", Address.class);

    // ================================================
    // External methods
    // ================================================

    /**
     * Creates a new token type and assigns _supply to creator
     *
     * @param _id     ID of the token
     * @param _supply The initial token supply
     * @param _uri    The token URI
     */
    @External
    public void mint(BigInteger _id, BigInteger _supply, String _uri) {
        Context.require(creators.get(_id) == null, "Token is already minted");
        Context.require(_supply.compareTo(BigInteger.ZERO) > 0, "Supply should be positive");

        final Address caller = Context.getCaller();
        creators.set(_id, caller);

        // mint tokens
        super._mint(caller, _id, _supply);
        // set token URI
        super._setTokenURI(_id, _uri);
    }

    protected void mint(BigInteger _id){
        super._mint(Context.getCaller(), _id, BigInteger.ONE);
    }

    /**
     * Destroys tokens for a given amount
     *
     * @param _id     ID of the token
     * @param _amount The amount of tokens to burn
     */
    @External
    public void burn(BigInteger _id, BigInteger _amount) {
        Context.require(creators.get(_id) != null, "Invalid token id");
        Context.require(_amount.compareTo(BigInteger.ZERO) > 0, "Amount should be positive");

        // burn tokens
        super._burn(Context.getCaller(), _id, _amount);
    }

    /**
     * Updates the given token URI
     *
     * @param _id  ID of the token
     * @param _uri The token URI
     */
    @External
    public void setTokenURI(BigInteger _id, String _uri) {
        Context.require(Context.getCaller().equals(creators.get(_id)), "Not token creator");

        super._setTokenURI(_id, _uri);
    }

    /**
     * @dev Returns the total amount of tokens stored by the contract.
     */
    @External(readonly = true)
    public BigInteger totalSupply() {
        return BigInteger.valueOf(tokenOwners.length());
    }

    @External(readonly = true)
    public Address ownerOf(BigInteger tokenId){
        return tokenOwners.get(tokenId);
    }

    /**
     * @dev Hook that is called before any token transfer.
     * This includes minting and burning.
     *
     * Calling conditions:
     *
     * - When `from` and `to` are both non-zero, ``from``'s `tokenId` will be
     * transferred to `to`.
     * - When `from` is zero, `tokenId` will be minted for `to`.
     * - When `to` is zero, ``from``'s `tokenId` will be burned.
     * - `from` cannot be the zero address.
     * - `to` cannot be the zero address.
     */
    @Override
    protected void _beforeTokenTransfer(
            Address from,
            Address to,
            BigInteger tokenId
    ) {
        super._beforeTokenTransfer(from, to, tokenId);

        if (to.equals(ZERO_ADDRESS)) {
            tokenOwners.remove(tokenId);
        } else if (to != from) {
            tokenOwners.set(tokenId, to);
        }

        if(!from.equals(ZERO_ADDRESS) && !to.equals(ZERO_ADDRESS) && from != to) {
            // transfer of token, user to user, to check later on might have to add _removetokenfromownerenumeration(from), _addtokentoownerEnumeration(to)
            tokenOwners.set(tokenId, to);
        }
    }

    @Override
    protected void _afterTokenTransfer(
            Address from,
            Address to,
            BigInteger tokenId
    ) {
        super._afterTokenTransfer(from, to, tokenId);
    }
}