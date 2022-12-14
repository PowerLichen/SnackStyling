from drf_spectacular.utils import extend_schema_view
from rest_framework import mixins
from rest_framework.decorators import action
from rest_framework.viewsets import GenericViewSet, ModelViewSet

import api.codi.schemas as CodiSchema
from api.codi.paginations import CodiListPagination
from api.codi.serializers import (CodiCreateSerializer, CodiDetailSerializer,
                                  CodiDuplicateSerializer, CodiSerializer,
                                  CodiUpdateSerializer,
                                  CodiUserCreateSerializer, CodiUserSerializer)
from api.permissions import UserAccessPermission
from model.codimodel.models import Codi


@extend_schema_view(
    create=CodiSchema.CODI_SCHEMA_CREATE,
    retrieve=CodiSchema.CODI_SCHEMA_RETRIEVE,
    partial_update=CodiSchema.CODI_SCHEMA_PARTIAL_UPDATE,
    destroy=CodiSchema.CODI_SCHEMA_DESTROY,
    list=CodiSchema.CODI_SCHEMA_LIST,
    dup_create=CodiSchema.CODI_SCHEMA_DUP,
)
class CodiViewSet(ModelViewSet):
    queryset = Codi.objects.all()
    serializer_class = CodiUserSerializer
    pagination_class = CodiListPagination
    permission_classes = [UserAccessPermission]

    def get_serializer_class(self):
        if hasattr(self, 'action') == False:
            return self.serializer_class

        if self.action == 'create':
            return CodiCreateSerializer
        if self.action == 'retrieve':
            return CodiDetailSerializer
        if self.action == 'partial_update':
            return CodiUpdateSerializer
        if self.action == 'destroy':
            return CodiSerializer
        if self.action == 'list':
            return CodiDetailSerializer
        if self.action == 'dup_create':
            return CodiDuplicateSerializer
        return self.serializer_class

    @action(detail=True, methods=['post'], url_path='dup')
    def dup_create(self, request, *args, **kwargs):
        request.data['id'] = self.kwargs['pk']
        super().partial_update(request, *args, **kwargs)
        return super().create(request, *args, **kwargs)


@extend_schema_view(
    create=CodiSchema.CODIUSER_SCHEMA_CREATE,
    list=CodiSchema.CODIUSER_SCHEMA_LIST
)
class CodiUserViewSet(mixins.CreateModelMixin,
                      mixins.ListModelMixin,
                      GenericViewSet):
    serializer_class = CodiSerializer
    pagination_class = CodiListPagination
    permission_classes = [UserAccessPermission]

    def get_queryset(self):
        pk = self.request.data['userId']
        return Codi.objects.filter(userId=pk)

    def get_serializer_class(self):
        if hasattr(self, 'action') == False:
            return self.serializer_class

        if self.action == 'create':
            return CodiUserCreateSerializer
        if self.action == 'list':
            return CodiDetailSerializer
        return self.serializer_class


class CodiExampleView(mixins.ListModelMixin,
                      GenericViewSet):
    serializer_class = CodiDetailSerializer

    def get_queryset(self):
        return Codi.objects.all().order_by('-id')[:10]
