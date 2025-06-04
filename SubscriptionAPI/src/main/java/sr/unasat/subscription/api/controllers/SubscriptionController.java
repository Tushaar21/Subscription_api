package sr.unasat.subscription.api.controllers;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import sr.unasat.subscription.api.dto.PricesDTO;
import sr.unasat.subscription.api.dto.SubscriptionDTO;
import sr.unasat.subscription.api.entity.Prices;
import sr.unasat.subscription.api.entity.Subscription;
import sr.unasat.subscription.api.services.SubscriptionService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;

@Path("/subscriptions")
public class SubscriptionController {
    private final SubscriptionService service = new SubscriptionService();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<SubscriptionDTO> getAllSubscriptions() {
        return service.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSubscription(@PathParam("id") int id) {
        Subscription sub = service.findById(id);
        if (sub != null) {
            return Response.ok(toDTO(sub)).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSubscription(SubscriptionDTO dto) {
        try {
            Subscription sub = toEntity(dto);
            Subscription saved = service.save(sub);
            return Response.status(Response.Status.CREATED).entity(toDTO(saved)).build();
        } catch (ConstraintViolationException e) {
            return buildValidationErrorResponse(e.getConstraintViolations());
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateSubscription(@PathParam("id") int id, SubscriptionDTO dto) {
        try {
            Subscription existing = service.findById(id);
            if (existing != null) {
                existing.setFirstname(dto.getFirstname());
                existing.setLastname(dto.getLastname());
                existing.setEmail(dto.getEmail());
                existing.setPhonenumber(dto.getPhonenumber());
                PricesDTO pricesDTO = dto.getSubscription();
                if (pricesDTO != null) {
                    Prices prices = new Prices();
                    prices.setId(pricesDTO.getId());
                    existing.setPrice(prices);
                } else {
                    existing.setPrice(null);
                }
                existing.setServices(dto.getServices());
                Subscription updated = service.update(existing);
                return Response.ok(toDTO(updated)).build();
            }
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (ConstraintViolationException e) {
            return buildValidationErrorResponse(e.getConstraintViolations());
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteSubscription(@PathParam("id") int id) {
        Subscription existing = service.findById(id);
        if (existing != null) {
            service.delete(id);
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    private SubscriptionDTO toDTO(Subscription sub) {
        PricesDTO pricesDTO = null;
        if (sub.getPrice() != null) {
            Prices prices = sub.getPrice();
            pricesDTO = new PricesDTO(
                    prices.getId(),
                    prices.getSubscriptionType(),
                    prices.getPrice(),
                    prices.getDescription()
            );
        }
        return new SubscriptionDTO(
            sub.getId(),
            sub.getFirstname(),
            sub.getLastname(),
            sub.getEmail(),
            sub.getPhonenumber(),
            pricesDTO,
            sub.getServices()
        );
    }

    private Subscription toEntity(SubscriptionDTO dto) {
        Prices prices = null;
        if (dto.getSubscription() != null) {
            PricesDTO pricesDTO = dto.getSubscription();
            prices = new Prices(
                    pricesDTO.getId(),
                    pricesDTO.getSubscriptionType(),
                    pricesDTO.getPrice(),
                    pricesDTO.getDescription()
            );
        }
        return new Subscription(
            dto.getFirstname(),
            dto.getLastname(),
            dto.getEmail(),
            dto.getPhonenumber(),
            prices,
            dto.getServices()
        );
    }

    private Response buildValidationErrorResponse(Set<ConstraintViolation<?>> violations) {
        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<?> v : violations) {
            String field = v.getPropertyPath().toString();
            errors.put(field, v.getMessage());
        }
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errors)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
