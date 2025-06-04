document.addEventListener('DOMContentLoaded', async () => {

    const togglePriceFormButton = document.getElementById('togglePriceFormButton');
    const priceManagementForm = document.getElementById('priceManagementForm');
    const pricesTableDiv = document.getElementById('pricesTable');
    const PRICES_API = 'http://localhost:7070/api/prices';

    // Toggle the price management form
    togglePriceFormButton.addEventListener('click', () => {
        priceManagementForm.style.display = priceManagementForm.style.display === 'none' ? 'block' : 'none';
    });

    // Handle price form submission
    priceManagementForm.addEventListener('submit', async (event) => {
        event.preventDefault();

        const subscriptionType = document.getElementById('priceSubscriptionType').value.trim();
        const priceAmount = document.getElementById('priceAmount').value.trim();
        const description = document.getElementById('priceDescription').value.trim();

        if (!subscriptionType || !priceAmount) {
            alert('Subscription type and price are required.');
            return;
        }

        const newPrice = {
            subscriptionType,
            price: parseFloat(priceAmount),
            description
        };

        const editId = priceManagementForm.dataset.editId;

        try {
            let response;
            if (editId) {
                response = await fetch(`${PRICES_API}/${editId}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(newPrice)
                });
            } else {
                response = await fetch(PRICES_API, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(newPrice)
                });
            }

            if (response.ok) {
                alert(editId ? 'Price updated successfully!' : 'Price added successfully!');
                priceManagementForm.reset();
                delete priceManagementForm.dataset.editId; // Clear edit mode
                priceManagementForm.querySelector('input[type="submit"]').value = 'Add Price';
                await fetchAndDisplayPrices(); // Refresh the table
                await populateSubscriptionOptions(); // Update subscription options
            } else {
                alert('Failed to save price. Please try again.');
            }
        } catch (error) {
            console.error('Error saving price:', error);
            alert('An error occurred while saving the price.');
        }
    });

    // Fetch and display prices table
    async function fetchAndDisplayPrices() {
        const priceDiv = document.getElementById('pricesTable');
        try {
            priceDiv.innerHTML = '<div class="loader"></div>';
            const response = await fetch(PRICES_API);
            if (response.ok) {
                const prices = await response.json();
                if (Array.isArray(prices) && prices.length > 0) {
                    let table = '<table class="subs-table"><thead><tr>' +
                        '<th>Subscription Type</th><th>Price</th><th>Description</th><th>Actions</th></tr></thead><tbody>';
                    prices.forEach(price => {
                        table += `<tr>` +
                            `<td>${price.subscriptionType}</td>` +
                            `<td>${price.price}</td>` +
                            `<td>${price.description}</td>` +
                            `<td style='min-width:120px; display:flex; gap:0.5em; align-items:center;'>` +
                            `<button class='edit-btn' data-id='${price.id}'>Edit</button>` +
                            `<button class='delete-btn' data-id='${price.id}'>Delete</button>` +
                            `</td></tr>`;
                    });
                    table += '</tbody></table>';
                    pricesTableDiv.innerHTML = table;
                    pricesTableDiv.style.display = 'block';

                    // Attach event listeners for edit/delete
                    document.querySelectorAll('.edit-btn').forEach(btn => {
                        btn.addEventListener('click', async (e) => {
                            const id = btn.getAttribute('data-id');
                            const price = prices.find(p => p.id == id);
                            if (price) {
                                document.getElementById('priceSubscriptionType').value = price.subscriptionType;
                                document.getElementById('priceAmount').value = price.price;
                                document.getElementById('priceDescription').value = price.description;
                                priceManagementForm.dataset.editId = id; // Store the ID for editing
                                priceManagementForm.querySelector('input[type="submit"]').value = 'Update Price';
                            }
                        });
                    });

                    document.querySelectorAll('.delete-btn').forEach(btn => {
                        btn.addEventListener('click', async (e) => {
                            const id = btn.getAttribute('data-id');
                            if (confirm('Are you sure you want to delete this price?')) {
                                await fetch(`${PRICES_API}/${id}`, { method: 'DELETE' });
                                alert('Price deleted successfully!');
                                await fetchAndDisplayPrices(); // Refresh the table
                                await populateSubscriptionOptions(); // Update subscription options
                            }
                        });
                    });
                } else {
                    pricesTableDiv.innerHTML = '<p>No prices found.</p>';
                    pricesTableDiv.style.display = 'block';
                }
            } else {
                pricesTableDiv.innerHTML = '<p>Failed to fetch prices. Please try again later.</p>';
            }
        } catch (error) {
            console.error('Error fetching prices:', error);
            pricesTableDiv.innerHTML = '<p>An error occurred while fetching prices.</p>';
        }
    }

    // Populate subscription options dynamically
    async function populateSubscriptionOptions() {
        const subscriptionOptionsDiv = document.getElementById('subscriptionOptions');
        try {
            const response = await fetch(PRICES_API);
            if (response.ok) {
                const prices = await response.json();
                if (Array.isArray(prices) && prices.length > 0) {
                    subscriptionOptionsDiv.innerHTML = prices.map(price => `
                        <input type="radio" id="subscription-${price.id}" name="subscription" value="${price.id}">
                        <label for="subscription-${price.id}">${price.subscriptionType} - $${price.price}</label>
                    `).join('');
                } else {
                    subscriptionOptionsDiv.innerHTML = '<p>No subscription types available.</p>';
                }
            } else {
                subscriptionOptionsDiv.innerHTML = '<p>Failed to load subscription types.</p>';
            }
        } catch (error) {
            console.error('Error fetching subscription types:', error);
            subscriptionOptionsDiv.innerHTML = '<p>Error loading subscription types.</p>';
        }
    }

    // Fetch prices and populate subscription options on page load
    await fetchAndDisplayPrices();
    await populateSubscriptionOptions();
});