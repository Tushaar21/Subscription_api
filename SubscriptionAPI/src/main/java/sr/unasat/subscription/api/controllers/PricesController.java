package sr.unasat.subscription.api.controllers;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import sr.unasat.subscription.api.dto.PricesDTO;
import sr.unasat.subscription.api.entity.Prices;
import sr.unasat.subscription.api.services.PricesService;

import java.util.List;
import java.util.stream.Collectors;

@Path("/prices")
public class PricesController {
    private final PricesService service = new PricesService();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<PricesDTO> getAllPrices() {
        return service.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPriceById(@PathParam("id") int id) {
        Prices price = service.findById(id);
        if (price != null) {
            return Response.ok(toDTO(price)).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createPrice(PricesDTO dto) {
        Prices price = toEntity(dto);
        Prices saved = service.save(price);
        return Response.status(Response.Status.CREATED).entity(toDTO(saved)).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePrice(@PathParam("id") int id, PricesDTO dto) {
        Prices existing = service.findById(id);
        if (existing != null) {
            existing.setSubscriptionType(dto.getSubscriptionType());
            existing.setPrice(dto.getPrice());
            existing.setDescription(dto.getDescription());
            Prices updated = service.update(existing);
            return Response.ok(toDTO(updated)).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deletePrice(@PathParam("id") int id) {
        Prices existing = service.findById(id);
        if (existing != null) {
            service.delete(id);
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    private PricesDTO toDTO(Prices price) {
        return new PricesDTO(
                price.getId(),
                price.getSubscriptionType(),
                price.getPrice(),
                price.getDescription()
        );
    }

    private Prices toEntity(PricesDTO dto) {
        return new Prices(
                dto.getId(),
                dto.getSubscriptionType(),
                dto.getPrice(),
                dto.getDescription()
        );
    }
}
