document.addEventListener('DOMContentLoaded', async () => {

    const form = document.getElementById('subscriptionRegistration');

    let isSubmitting = false;
    const submitBtn = form.querySelector('input[type="submit"]');
    const successBanner = document.createElement('div');
    successBanner.id = 'successBanner';
    successBanner.style.display = 'none';
    successBanner.textContent = 'Subscription registered successfully!';
    successBanner.className = 'success-banner';
    form.parentNode.insertBefore(successBanner, form);

    const spinner = document.createElement('div');
    spinner.id = 'spinner';
    spinner.style.display = 'none';
    spinner.innerHTML = '<div class="loader"></div>';
    form.appendChild(spinner);

    let editId = null;
    const editBanner = document.getElementById('editBanner');
    const filterInput = document.getElementById('filterInput');
    const cancelEditBtn = document.getElementById('cancelEditBtn');

    const API_BASE = 'http://localhost:7070/api/subscriptions';

    filterInput.addEventListener('input', () => {
        getAllSubscriptions(filterInput.value);
    });

    form.addEventListener('reset', () => {
        editId = null;
        editBanner.style.display = 'none';
        submitBtn.value = 'Register';
        cancelEditBtn.style.display = 'none';
    });

    cancelEditBtn.addEventListener('click', () => {
        form.reset();
        editId = null;
        editBanner.style.display = 'none';
        submitBtn.value = 'Register';
        cancelEditBtn.style.display = 'none';
    });

    form.addEventListener('submit', async (event) => {
        event.preventDefault();
        if (isSubmitting) return;
        isSubmitting = true;
        submitBtn.disabled = true;
        spinner.style.display = 'block';
        successBanner.style.display = 'none';
        editBanner.style.display = 'none';

        let isValid = true;

        const fields = ['firstname', 'lastname', 'email', 'phonenumber'];
        fields.forEach(field => {
            const input = document.getElementById(field);
            const validationMessage = document.getElementById(`${field}-validation`);
            if (!input.value.trim()) {
                validationMessage.style.display = 'block';
                isValid = false;
            } else {
                validationMessage.style.display = 'none';
            }
        });

        // Clear previous backend errors
        fields.forEach(field => {
            const validationMessage = document.getElementById(`${field}-validation`);
            validationMessage.textContent = `${field.charAt(0).toUpperCase() + field.slice(1).replace('phonenumber', 'Phone number')} is required.`;
        });

        if (isValid) {
            const formData = new FormData(form);

            const data = {
                firstname: formData.get('firstname'),
                lastname: formData.get('lastname'),
                email: formData.get('email'),
                phonenumber: formData.get('phonenumber'),
                subscription: {id: parseInt(formData.get('subscription'))},
                services: formData.getAll('services').join(',')
            };

            try {
                let response;
                if (editId) {
                    response = await fetch(`${API_BASE}/${editId}`, {
                        method: 'PUT',
                        headers: {'Content-Type': 'application/json'},
                        body: JSON.stringify(data)
                    });
                } else {
                    response = await fetch(API_BASE, {
                        method: 'POST',
                        headers: {'Content-Type': 'application/json'},
                        body: JSON.stringify(data)
                    });
                }
                if (response.ok) {
                    const msg = editId ? 'Subscription updated successfully!' : 'Subscription registered successfully!';
                    alert(msg);
                    successBanner.textContent = msg;
                    successBanner.style.display = 'block';
                    setTimeout(() => {
                        successBanner.style.display = 'none';
                    }, 3000);
                    getAllSubscriptions(filterInput.value);
                    form.reset();
                    editId = null;
                    submitBtn.value = 'Register';
                    cancelEditBtn.style.display = 'none';
                } else if (response.status === 400) {
                    const errorObj = await response.json();
                    Object.keys(errorObj).forEach(field => {
                        const validationMessage = document.getElementById(`${field}-validation`);
                        if (validationMessage) {
                            validationMessage.textContent = errorObj[field];
                            validationMessage.style.display = 'block';
                        }
                    });
                } else {
                    const errorText = await response.text();
                    alert('Error: ' + errorText);
                }
            } catch (error) {
                alert('Error submitting form: ' + error);
            }
        } else {
            console.log('Form is not valid');
        }
        isSubmitting = false;
        submitBtn.disabled = false;
        spinner.style.display = 'none';
    });

    await getAllSubscriptions();

    async function getAllSubscriptions(filter = '') {
        const subscriptionsDiv = document.getElementById('subscriptions');
        // Always render the search input at the top
        let searchHtml = `
            <div class="form-group" style="margin-bottom:0.5em;">
                <input type="text" id="filterInput" value="${filter || ''}" placeholder="Search by name, email, or phone..." style="width:100%;padding:0.7em;border-radius:0.5em;border:1px solid #d1d5db;font-size:1em;">
            </div>
        `;
        try {
            subscriptionsDiv.innerHTML = searchHtml + '<div class="loader"></div>';
            const response = await fetch(API_BASE);
            let subscriptions = await response.json();
            if (filter) {
                const f = filter.toLowerCase();
                subscriptions = subscriptions.filter(sub =>
                    sub.firstname.toLowerCase().includes(f) ||
                    sub.lastname.toLowerCase().includes(f) ||
                    sub.email.toLowerCase().includes(f) ||
                    sub.phonenumber.toLowerCase().includes(f)
                );
            }
            if (Array.isArray(subscriptions) && subscriptions.length > 0) {
                let table = '<div style="overflow-x:auto;"><table class="subs-table"><thead><tr>' +
                    // Remove the ID column from the header
                    '<th>First Name</th><th>Last Name</th><th>Email</th><th>Phone</th><th>Type</th><th>Channels</th><th>Actions</th></tr></thead><tbody>';
                subscriptions.forEach(sub => {
                    const services = sub.services ? sub.services.split(',').map(s => s.trim()).filter(Boolean).join(', ') : '';
                    table += `<tr>` +
                        `<td>${sub.firstname}</td><td>${sub.lastname}</td><td>${sub.email}</td><td>${sub.phonenumber}</td>` +
                        `<td>${sub.subscription ? sub.subscription.subscriptionType : ''}</td>` +
                        `<td>${services}</td>` +
                        `<td style='min-width:120px; display:flex; gap:0.5em; align-items:center;'>` +
                        `<button class='edit-btn' data-id='${sub.id}'>Edit</button>` +
                        `<button class='delete-btn' data-id='${sub.id}'>Delete</button>` +
                        `</td></tr>`;
                });
                table += '</tbody></table></div>';
                subscriptionsDiv.innerHTML = searchHtml + table;
                // Attach event listeners for edit/delete
                document.querySelectorAll('.edit-btn').forEach(btn => {
                    btn.addEventListener('click', async (e) => {
                        const id = btn.getAttribute('data-id');
                        const sub = subscriptions.find(s => s.id == id);
                        if (sub) {
                            document.getElementById('firstname').value = sub.firstname;
                            document.getElementById('lastname').value = sub.lastname;
                            document.getElementById('email').value = sub.email;
                            document.getElementById('phonenumber').value = sub.phonenumber;
                            // Set radio
                            if (sub.subscription) {
                                document.querySelectorAll('input[name="subscription"]').forEach(radio => {
                                    radio.checked = radio.value === sub.subscription;
                                });
                            }
                            // Set checkboxes
                            document.querySelectorAll('input[name="services"]').forEach(checkbox => {
                                checkbox.checked = false;
                            });
                            if (sub.services) {
                                sub.services.split(',').forEach(val => {
                                    const cb = document.querySelector(`input[name='services'][value='${val.trim()}']`);
                                    if (cb) cb.checked = true;
                                });
                            }
                            editId = id;
                            editBanner.style.display = 'block';
                            submitBtn.value = 'Update';
                            cancelEditBtn.style.display = 'inline-block';
                            // Scroll to the form (down)
                            form.scrollIntoView({behavior: 'smooth', block: 'start'});
                        }
                    });
                });
                document.querySelectorAll('.delete-btn').forEach(btn => {
                    btn.addEventListener('click', async (e) => {
                        const id = btn.getAttribute('data-id');
                        if (confirm('Are you sure you want to delete this subscription?')) {
                            await fetch(`${API_BASE}/${id}`, {method: 'DELETE'});
                            alert('Subscription deleted successfully!');
                            getAllSubscriptions(filterInput.value);
                        }
                    });
                });
            } else {
                subscriptionsDiv.innerHTML = searchHtml + '<p>No subscriptions found.</p>';
            }
        } catch (error) {
            subscriptionsDiv.innerHTML = searchHtml + '<div style="color:red; padding:1em; background:#fff3f3; border:1px solid #f5c2c7; border-radius:0.5em;">' +
                'Error fetching subscriptions.<br>' +
                'Please check if the backend server is running and accessible at <b>http://localhost:7070/api/subscriptions</b>.' +
                '<br><span style="font-size:0.95em;">Technical details: ' + error + '</span></div>';
            console.error('Error fetching subscriptions:', error);
        }
        // Re-attach filter event listener after re-render
        const filterInputEl = document.getElementById('filterInput');
        if (filterInputEl) {
            filterInputEl.addEventListener('input', () => {
                getAllSubscriptions(filterInputEl.value);
            });
        }
    }

    // Set selected subscription type on edit
    function setSelectedSubscriptionType(subscriptionId) {
        const radio = document.querySelector(`input[name="subscription"][value="${subscriptionId}"]`);
        if (radio) {
            radio.checked = true;
        }
    }

    // Validate subscription type
    function validateSubscriptionType() {
        const selected = document.querySelector('input[name="subscription"]:checked');
        const validationMessage = document.getElementById('subscription-validation');
        if (!selected) {
            validationMessage.style.display = 'block';
            return false;
        }
        validationMessage.style.display = 'none';
        return true;
    }

    // Call populateSubscriptionOptions on page load
    await populateSubscriptionOptions();

    // Example usage in form submission
    form.addEventListener('submit', async (event) => {
        event.preventDefault();
        if (!validateSubscriptionType()) return;

        // Rest of the form submission logic...
    });

});
